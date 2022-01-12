import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';

import {ApiOrchMeasureBoxImpl, IEdition} from '../editing.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EditionService } from '../service/edition.service';
import { ParseLinks } from 'app/core/util/parse-links.service';

import * as OpenSeadragon from 'openseadragon';
import * as OSDAnnotorious from '@recogito/annotorious-openseadragon';
import ShapeLabelsFormatter from '@recogito/annotorious-shape-labels';
import {TileSource} from "openseadragon";
import {StorageService} from "../../edition-detail/service/edition-storage.service";
import {IMeasureBox} from "../../../entities/measure-box/measure-box.model";
import {ApiOrchMeasureBox} from "../../../shared/model/openapi/model/apiOrchMeasureBox";
@Component({
  selector: 'jhi-edition',
  templateUrl: './editing.component.html',
  // styleUrls: ['@recogito/annotorious/dist/annotorious.min.css']
})
export class EditingComponent implements OnInit {

  BASEURL = "/api/";
  // TODO do not allow out of boundary calls
  currentPage = 0;
  lastPage = -1;
  status = "SAVED"
  offset = 0;
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  predicate: string;
  ascending: boolean;
  annotationsData:any[] = [];
  currentMeasureNo = 1;
  viewer: any;
  anno: any;

  constructor(protected storageService: StorageService,protected activatedRoute: ActivatedRoute, protected editionService: EditionService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  ngOnInit(): void {

    this.editionService.fetchTotalPageCount(this.storageService.getActiveEditionId())
      .subscribe(pageCount=> {
          if(typeof(pageCount) === "number") {
            this.lastPage = pageCount;
          }
        }
      )

    this.editionService.fetchMeasureBoxes(this.storageService.getActiveEditionId(), this.currentPage)
      .subscribe(x => console.warn(x));

    this.viewer = OpenSeadragon({
        // ajaxWithCredentials:true,
        loadTilesWithAjax: true,
        // ajaxWithCredentials: true,
        ajaxHeaders: {
          'Accept': 'image/avif,image/webp,image/png,image/svg+xml,image/*,*/*;q=0.8',
          'Authorization': 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTY0MDgwNTU0NH0.GP6vgrAwLzx1KtGJblptm-iY_0_wUmZk8KQmY2CD4cCsJA77o5ypeXIV29E6hXJc3W-YOen1iU-fdtd3KS3_Yw',
          'Accept-Encoding': 'gzip, deflate, br',
          'Accept-Language': 'en-US,en;q=0.9',
          'Connection': 'keep-alive'
          // 'Sec-Fetch-Mode': 'no-cors',
          // 'Sec-Fetch-Size': 'same-site',
          // 'Sec-Fetch-Dest': 'image',
          // 'Authentication': 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTY0MDcyNjg4MX0.D4AxgHIB1Y4TFkqxGbHDCQIgHjki707JvECXZx7Z5m55hAiZkCrUBZjf8CeYgO-6egWOE18ShhWmm73oKieHSA'
        },
        id: "viewer",
        prefixUrl: "openseadragon/images/",
        minZoomLevel: 	0.1,
        maxZoomLevel: 	13,
        tileSources:
          {
            type: 'image',
            url: this.generateUrl(this.storageService.getActiveEditionId(), this.currentPage)
          },

        // tileSources: 'https://www.bsb-muenchen.de/fileadmin/bsb/sammlungen/musik/aktuelles/strauss_richard_metamorphosen_ausschnitt.jpg'
        // tileSources: {
        //     type: 'image',
        //   ajaxWithCredentials: true,
        //   ajaxHeaders: {
        //     'Accept': 'image/avif,image/webp,image/png,image/svg+xml,image/*,*/*;q=0.8',
        //     'tset': 'test'
        //   },
        //   url: 'https://banner2.cleanpng.com/20180419/hkw/kisspng-ssc-mts-exam-test-computer-icons-educational-entra-test-paper-5ad919071997b8.5830873915241771591048.jpg'
        // }
        // url: 'https://www.pngall.com/wp-content/uploads/4/World-Wide-Web-PNG-Free-Image.png'
        // url: 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/Test-Logo.svg/783px-Test-Logo.svg.png'
        //  url:
        // }
        // visibilityRatio: 0.1,
        // constrainDuringPan: true,
        // tileSources: {
        //   type: 'image',
        //   url: 'http://localhost:12321/api/edition/24/getPage/2'
        // }
      }
    );
    // this.viewer.ajaxHeaders = {
    //   "asd":"asdf"
    // }
    this.setAnnotationsWithServerData();
  }

  nextPage(): void
  {
    // if(this.currentPage < this.lastPage ) {
      this.viewer.open({
        type: 'image',
        // ajaxWithCredentials: true,
        // loadTilesWithAjax: true,
        // ajaxHeaders: {
        //   'Authentication': 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTY0MDcyNjg4MX0.D4AxgHIB1Y4TFkqxGbHDCQIgHjki707JvECXZx7Z5m55hAiZkCrUBZjf8CeYgO-6egWOE18ShhWmm73oKieHSA'
        // },
        // url: this.generateUrl(this.storageService.getActiveEditionId(), this.currentPage)
        url: this.generateUrl(this.storageService.getActiveEditionId(), ++this.currentPage)
      })
      this.setAnnotationsWithServerData();
    // }
  }


  initializeAnnotorious(): void {
    // Note: Although methods like clearAnnotations() and setAnnotations() exist, we have to recreate the Annotorious Instance
    // to prevent internal conflicts with the Annotations
    if(this.anno){
      this.anno.destroy()
    }
    this.anno = OSDAnnotorious(this.viewer, {
      formatter: ShapeLabelsFormatter(),
      // disableEditor: true
    });
    this.anno.setAnnotations(this.annotationsData);
    // this.anno.setAnnotations(this.getExampleAnnotation());

    this.anno.on('createSelection', async (selection:any) => {
      selection.body = [{
        type: 'TextualBody',
        purpose: 'tagging',
        value: this.currentMeasureNo++ + this.offset
      }];
      await this.anno.updateSelected(selection);
      await this.anno.saveSelected();
      this.annotationsData = this.anno.getAnnotations();
      this.status = "UNSAVED";
    });

    this.anno.on('updateAnnotation', () => {
      this.annotationsData = this.anno.getAnnotations();
      this.status = "UNSAVED";
    });

    this.anno.on('deleteAnnotation', () => {
      this.annotationsData = this.anno.getAnnotations();
      // this.currentMeasureNo--; // Maybe this makes sense, but deleting something in between might be unlogic
      this.status = "UNSAVED";
    });
  }

  previousPage(): void
  {
    if(this.currentPage > 0) {
      this.viewer.open({
        type: 'image',
        // ajaxWithCredentials: true,
        // loadTilesWithAjax: true,
        // ajaxHeaders: {
        //   'Authentication': 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTY0MDcyNjg4MX0.D4AxgHIB1Y4TFkqxGbHDCQIgHjki707JvECXZx7Z5m55hAiZkCrUBZjf8CeYgO-6egWOE18ShhWmm73oKieHSA'
        // },
        // url: this.generateUrl(this.storageService.getActiveEditionId(), this.currentPage)
        url: this.generateUrl(this.storageService.getActiveEditionId(), --this.currentPage)
      })
      this.setAnnotationsWithServerData();
    }
  }
  generateUrl(edition: number | boolean | undefined, page: number): string {
    return this.BASEURL + 'edition/' +String(edition)+ "/getPage/" + String(page);
  }

  trackId(index: number, item: IEdition): number {
    return item.id!;
  }

  previousState(): void {
    window.history.back();
  }

  setAnnotationsWithServerData(): void {
    this.editionService.fetchMeasureBoxes(this.storageService.getActiveEditionId(), this.currentPage)
      .subscribe(response => {
          this.editionService.fetchBoxOffset(this.storageService.getActiveEditionId(), this.currentPage)
            .subscribe((res) => {
              if(response.body) {
                if(typeof(res) === "number") {
                  this.offset = res;
                }
                this.annotationsData = this.makeW3CConform(response.body)
                this.currentMeasureNo = 1;
                this.initializeAnnotorious()
              }
            });
      });
  }
  save(): void {
    if(this.status === "UNSAVED") {
      const measureBoxListConvertedBack = this.convertBack();
      console.warn(measureBoxListConvertedBack)
      this.editionService.save(this.storageService.getActiveEditionId(), this.currentPage, measureBoxListConvertedBack)
        .subscribe(x=>{
          this.status = "SAVED"
        });
    }
  }

  private convertBack(): ApiOrchMeasureBox[] {
    const measureBoxes = []
    for (const annotation of this.annotationsData)  {
      const restoredValues = this.restoreValues(annotation);
      console.warn(restoredValues)
      measureBoxes.push(
        new ApiOrchMeasureBoxImpl(
          undefined,
          restoredValues[0], // ulx
          restoredValues[2] + restoredValues[0], // lry
          restoredValues[3] + restoredValues[1], //lrx
          restoredValues[1], //uly
          annotation.body[0].value - this.offset,
          "Edited"
        )
      );
    }
    return measureBoxes;
  }

  private restoreValues(annotation:any):number[] {
    const simplifiedRectangleString = annotation.target.selector.value;
    const splittedRectangleString:string[] = simplifiedRectangleString.replace("xywh=pixel:","").split(",");

    return [
      Number(splittedRectangleString[0]),
      Number(splittedRectangleString[1]),
      Number(splittedRectangleString[2]),
      Number(splittedRectangleString[3]),
    ]
  }


  private makeW3CConform(boxes:ApiOrchMeasureBox[]): any[] {
    const w3cJson = []
    for (const val of boxes) {
      w3cJson.push(this.createAnnotationForJson(val))
    }
    return w3cJson;
  }

  private calcRectangleValues(mb: ApiOrchMeasureBox): string {
    let w = 0;
    let h = 0;
    if(mb.lrx && mb.ulx && mb.uly && mb.lry) {
      w = mb.lrx - mb.ulx;
      h = mb.lry - mb.uly;
    }
    return "xywh=pixel:" + String(mb.ulx) + "," + String(mb.uly) + "," + String(w) + "," + String(h);
  }


  private createAnnotationForJson(mb: ApiOrchMeasureBox): any {
    let measureCount = 0;
    if(typeof (mb.measureCount) === "number") {
      measureCount = mb.measureCount + this.offset;
    }
    return {
      "@context": "http:/www.w3.org/ns/anno.jsonId",
      "id": String(mb.id),
      "type": "Annotation",
      "body":  [{
        "type": "textualBody",
        "purpose": "tagging",
        "value": measureCount
      }],
      "target": {
        "selector": {
          "type": "FragmentSelector",
          "conformsTo": "http://www.w3.org/TR/media-frags/",
          "value": this.calcRectangleValues(mb)
        }
      }
      // TODO Support for more comments later on!
    }
  }

}

