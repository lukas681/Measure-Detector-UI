package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.MeasureBox;
import com.rs.detector.domain.Page;
import com.rs.detector.repository.EditionRepository;
import com.rs.detector.repository.PageRepository;
import com.rs.detector.service.*;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import com.rs.detector.service.measureDetection.MeasureDetectorService;
import com.rs.detector.service.util.FileUtilService;
import com.rs.detector.web.api.model.ApiMeasureDetectorResult;
import com.rs.detector.web.api.model.ApiOrchMeasureBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jobrunr.jobs.context.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This service projects the process of editing music. Therefore, it provides methods to create new Editins along with the corresponding scanned sheets.
 */
@Service
public class EditingService {

    private final Logger log = LoggerFactory.getLogger(EditingService.class);
    private final long DEFAULT_OFFSET = 0;

    @Autowired
    FileUtilService fileUtilService;

    @Autowired
    EditingFileManagementService editingFileManagementService;

    @Autowired
    EditionRepository editionRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    PageService pageService;

    @Autowired
    MeasureBoxService measureBoxService;

    @Autowired
    MeasureDetectorService measureDetectorService;

    @Autowired
    ScorePageService scorePageService;

    @Autowired
    ProjectService projectService;

    @Autowired
    EditionService editionService;

    @Autowired
    MeiService meiService;

    /**
     * Provides a conventient way to upload a new Edition with a corresponding PDF file which will later be splitted
     * and analysed by the Measure Detector.
     * @param e The Edition
     * @param pdfFile A PDF file with the music sheet
     */
    public void uploadNewEdition(Edition e, PDDocument pdfFile) throws IOException {
        if(e.getProject() == null) {
            var p = projectService.findOne(e.getProjectId())
                .toProcessor().block();
            e.setProject(p);
            p.addEditions(e);
            projectService.save(p).toProcessor().block();
        }
        editionService.save(e).toProcessor().block();
        editingFileManagementService.storePDFfile(e, pdfFile);
    }

    /**
     * Extracts  the pages as images from an Edition
     * @param e
     */
    public void extractImagesFromPDF(Edition e, JobContext jobContext) throws IOException {
        editingFileManagementService.extractPagesFromEdition(e, jobContext);
    }

    /**
     * Triggeres the Measure Detection on all Pages. Requires the methods and uploaded Edition and the extracted
     * Images, so make sure to call that beforehand. For example:
     *
     * editingService.uploadNewEdition(testEdition, pdf);
     * editingService.extractImagesFromPDF(testEdition);
     * scorePageService.generatePageObjectIfNotExistent(testEdition);
     *
     * @param e the complete edition to be processed
     * @param jobContext
     * @throws IOException
     */
    public void runFullMeasureDetectionOverEdition(@NotNull Edition e, JobContext jobContext) throws IOException {

        // TODO Delete Everything if measure Detection has been run
        var allGeneratedAvailableScorePages = editingFileManagementService.getAllGeneratedScorePageFilesAsPageNr(e);

        for(int i = 0; i < allGeneratedAvailableScorePages.size(); i++) {
            logInfo("Working on Page " + (i+1) + " out of " + allGeneratedAvailableScorePages.size(), jobContext);
            logInfo("Name of the current Page: " + allGeneratedAvailableScorePages.get(i), jobContext);
            runMeasureDetectionForSinglePage(e, Long.valueOf(allGeneratedAvailableScorePages.get(i)));
        }
        this.recalculatePageOffsets(e);
    }

    private void runMeasureDetectionForSinglePage(Edition e, Long pageNr) throws IOException {
        var measureDetectorResult = runMeasureDetectionOnEdition(e, pageNr);
        var p =
            searchPageInRepository(e, pageNr);
        if(p.isPresent()) {
            scorePageService.updatePageMeasureBoxesWithMDResult(p.get(), measureDetectorResult)
                .collectList()
                .toProcessor()
                .block();
        } else {
            log.error("The desired page " + pageNr +" could not be found. Maybe it has not been created so far?!");
        }
    }

    public Optional<Page> searchPageInRepository(Edition e, Long  pageNr) {
        return pageRepository.findAllByEditionId(e.getId())
            .collect(Collectors.toList()).toProcessor().block()
            .stream().filter(x -> x.getPageNr().equals(pageNr))
            .collect(Collectors.toList()).stream()
            .findFirst();
    }

    public Resource getPageResourceToEditionAndPageNr(Edition e, Long pageNr) throws FileNotFoundException, MalformedURLException {
        return editingFileManagementService.getPage(e, Math.toIntExact(pageNr));
    }

    public Resource getPageResourceToEditionAndPageNr(long e, Long pageNr) throws FileNotFoundException, MalformedURLException {
        var edition = editionService.findOne(e).toProcessor().block();

        return editingFileManagementService.getPage(edition, Math.toIntExact(pageNr));
    }

    public ApiMeasureDetectorResult runMeasureDetectionOnEdition(@NotNull Edition e, long pageNr) throws IOException {
        File f = new File(
            editingFileManagementService.constructPagePath(e, (int) pageNr)
        );
        return measureDetectorService.process(f);
    }

    public EditingFileManagementService getEditingFileManagementService() {
        return this.editingFileManagementService;
    }

//    public void runMeasureDetectionOnEdition(Integer id) throws IOException {
//        var e = editionService.findOne(Long.valueOf(id)).toProcessor().block();
//        if(e != null) {
//            // run the whole edition
//            runFullMeasureDetectionOverEdition(e, );
//        }
//    }

    public List<ApiOrchMeasureBox> getMeasureBoxesbyEditionIDandPageNr(Integer editionID, Long valueOf) {
        List<ApiOrchMeasureBox> result = new ArrayList<>();
        Edition e = getEdition(editionID, editionService);
        assert e != null;
        var page = searchPageInRepository(e, valueOf);

        if(page.isPresent()) {
             result = measureBoxService.findAllByPageId(page.get().getId())
                .collectList()
                .toProcessor()
                .block()
                 .stream().map(EditingService::MeasureBoxToApiOrch)
                 .collect(Collectors.toList());
        }
        return result;
    }

    public static ApiOrchMeasureBox MeasureBoxToApiOrch(MeasureBox measureBox) {
        assert(measureBox != null);
        return new ApiOrchMeasureBox()
            .comment(measureBox.getComment())
            .id(measureBox.getId())
            .measureCount(measureBox.getMeasureCount())
            .lrx(measureBox.getLrx())
            .lry(measureBox.getLry())
            .ulx(measureBox.getUlx())
            .uly(measureBox.getUly());
    }
    private void logInfo(String s, JobContext jobContext) {
        if(jobContext != null) {
            jobContext.logger().info(s);
            log.info(s);
        }
    }

    /**
     * Takes an edition and fetches all pages. Calling this method only makes sense, if MeasureBoxes have already
     * been generated.
     * Although this method makes a lot of database calls: Iterating over all pages
     *
     * Note: I decided against using the nextPage property, because errorious data might easly lead to endles
     * recursions if not caught. So it might be easier just to fetch all pages and sort them by page nr
     */
    public void recalculatePageOffsets(Edition e) {
        var pages = pageRepository
            .findAllByEditionId(e.getId())
            .collectList()
            .toProcessor()
            .block();
        assert(pages != null);
        pages.sort(Comparator.comparing(Page::getPageNr));
        updatePagesWithMeasureBoxCounts(pages);
    }

    private void updatePagesWithMeasureBoxCounts(List<Page> pages) {
        long idx = 0;
        for(var page: pages) {
            var boxes = measureBoxService.findAllByPageId(page.getId())
                .collectList()
                .toProcessor()
                .block();
            page.setMeasureNumberOffset(idx);
            pageRepository.update(page)
                .toProcessor()
                .block();
            if(boxes != null) {
                idx+=boxes.size();
            }
        }
    }

    public Long getMeasureBoxesOffsetbyEditionIDandPageNr(Integer editionID, Long pageNr) {
        Edition e = getEdition(editionID, editionService);
        assert e != null;
        var page = searchPageInRepository(e, pageNr);
        if(page.isPresent()) {
            return page.get().getMeasureNumberOffset();
        }
        return DEFAULT_OFFSET;

    }

    private Edition getEdition(Integer editionID, EditionService editionService) {
        assert(editionID != null);
        var e = editionService
            .findOne(Long.valueOf(editionID))
            .toProcessor()
            .block();
        return e;
    }

    public void saveMeasureBoxesbyEditionIdAndPageNr(Integer editionID, Integer pageNr, List<ApiOrchMeasureBox> apiOrchMeasureBox) {
        assert(pageNr != null);
        var edition = getEdition(editionID, editionService);

        var page =
            searchPageInRepository(edition, Long.valueOf(pageNr));
        System.out.println(page.isPresent());
        if(page.isPresent()){
            log.debug("Page Found. Now Deleting existing Measure Boxes");
            pageService.deleteAllMeasureBoxes(page.get().getId())
                .collectList()
                .toProcessor()
                .block();
            System.out.println("HIER");
            log.debug("Success now converting the new boxes");

            var measureBoxes =
                apiOrchMeasureBox
                    .stream()
                    .map(x-> convertMeasureBoxApiOrch(page.get(), x))
                    .collect(Collectors.toList());

            saveAllMeasureBoxesUnblocked(measureBoxes);
            log.debug("Saved the new Measure Boxes!");
        } else {
            log.debug("The required page could not be found!");
        }
    }

    private void saveAllMeasureBoxesUnblocked(List<MeasureBox> measureBoxes) {
        log.debug("Saving new Measure Boxes");
        measureBoxService.saveAll(measureBoxes)
            .collectList()
            .toProcessor()
            .block();
    }

    private MeasureBox convertMeasureBoxApiOrch(Page p, ApiOrchMeasureBox mb) {
        return new MeasureBox()
            .comment(mb.getComment())
            .measureCount(mb.getMeasureCount())
            .page(p)
            .lrx(mb.getLrx())
            .lry(mb.getLry())
            .ulx(mb.getUlx())
            .uly(mb.getUly());
    }

    /**
     * This method constructs a resulting pdf from the pngs annotated with the measures
     */
    public String createPdfWithMeasures(Edition e) throws IOException, PagesMightNotHaveBeenGeneratedException {
        return createPdf(e, true);
    }

    public String createPDFWithoutMeasures(Edition e) throws PagesMightNotHaveBeenGeneratedException, IOException {
        return createPdf(e, false);
    }

    /**
     * Generates the PDF with annotated Measure Boxes
     * @param e
     * @param hasMeasures
     * @return
     * @throws IOException
     * @throws PagesMightNotHaveBeenGeneratedException
     */
    public String createPdf(Edition e, boolean hasMeasures) throws IOException,
        PagesMightNotHaveBeenGeneratedException {
        String generatedPath = null;
        for(var pn: editingFileManagementService.getAllGeneratedScorePageFilesAsPageNr(e)) {
            var p= this.searchPageInRepository(e, Long.valueOf(pn));
            if(p.isPresent()) { // We could also do an assert, but this relaxes it a bit
                BufferedImage img = editingFileManagementService.loadPage(e, pn); // TODO maybe replace with Page
                if(hasMeasures) {
                    scorePageService.addMeasureBoxesToBufferedImage(img, p.get());
                }
                editingFileManagementService.writeBufferedInEditionTmpFolder(img, e, p.get());
            }
        }
        generatedPath = editingFileManagementService.combineImagesIntoPDF(e);
        editingFileManagementService.deleteEditionSubfolder(e, "/tmp");
        return generatedPath;
    }

    public void recalculatePageOffsets(Integer editionID) {
        recalculatePageOffsets((getEdition(editionID,editionService)));
    }

    public InputStream getGeneratedPDFFile(Edition e) throws FileNotFoundException {
        return fileUtilService.getPDF(editingFileManagementService.constructAnnotatedPDFPath(e));
    }

    public String generateMEI(Edition edition) throws IOException {
        return meiService.generateMEIXML(edition);
    }
}
