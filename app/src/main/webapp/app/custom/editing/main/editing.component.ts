import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';

import {ApiOrchMeasureBoxImpl, IEdition} from '../editing.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EditionService } from '../service/edition.service';
import { ParseLinks } from 'app/core/util/parse-links.service';

import * as OpenSeadragon from 'openseadragon';
import * as OSDAnnotorious from '@recogito/annotorious-openseadragon';
import ShapeLabelsFormatter from '@recogito/annotorious-shape-labels';
import {StorageService} from "../../edition-detail/service/edition-storage.service";
import {ApiOrchMeasureBox} from "../../../shared/model/openapi/model/apiOrchMeasureBox";
enum Status {
  SAVED = "Saved",
  MODIFIED = "Modified"
}
// TODO Improvement: Add array with all used numbers and jump to unused ones.

@Component({
  selector: 'jhi-edition',
  templateUrl: './editing.component.html',
  // styleUrls: ['@recogito/annotorious/dist/annotorious.min.css']
})
export class EditingComponent implements OnInit {

  BASEURL = "/api/";
  // TODO do not allow out of boundary calls
  currentPage = 1;
  lastPage = -1;
  status = Status.SAVED
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
  requestedPage: number | undefined;
  newMeasureValue: any;

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

    this.viewer = OpenSeadragon({
        // ajaxWithCredentials:true,
        loadTilesWithAjax: true,
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
            url: this.generateUrl(this.storageService.getActiveEditionId(), this.currentPage - 1)
          },
      }
    );
    this.setAnnotationsWithServerData();
  }

  nextPage(): void {
    if(this.lastPage === 0 || this.currentPage < this.lastPage) {
      this.viewer.open({
        type: 'image',
        url: this.generateUrl(this.storageService.getActiveEditionId(), ++this.currentPage - 1)
      })
      this.setAnnotationsWithServerData();
    }
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
      this.status = Status.MODIFIED;
    });

    this.anno.on('updateAnnotation', (updated:any, prev:any) => {
      this.annotationsData = this.anno.getAnnotations();
      const hasTagChanged = prev.body[0].value !== updated.body[0].value;
      // If the measure Number was changed, we have to do a few steps:
      if(updated.body[0].value !== undefined) {
       this.currentMeasureNo = Number(updated.body[0].value)+ 1;
      }
      if(hasTagChanged) { // Has changed
        this.updateInInterval(updated.body[0].value, prev.body[0].value, updated)
        this.anno.setAnnotations(this.annotationsData)
      }
      this.annotationsData = this.anno.getAnnotations();
      this.status = Status.MODIFIED;
    });

    // TODOS: set curser to tag + 1
   this.anno.on('deleteAnnotation', (selection:any) => {
      const deletedMeasureNumber:number = selection.body[0].value;
      this.annotationsData = this.anno.getAnnotations();
      // this.currentMeasureNo--; // Maybe this makes sense, but deleting something in between might be unlogical
      // TODO decrease all annotations that come after this
      this.updateFollowingMeasures(deletedMeasureNumber, -1)
      this.anno.setAnnotations(this.annotationsData);
      this.status = Status.MODIFIED;
    });
  }

  updateInInterval(newMeasureNo: number, oldMeasureNo: number, updated:any): void {
    this.annotationsData.filter(
      (el) => {
        if (el.body[0].value !== undefined) {
          console.warn(el === updated)
          return el.body[0].value >= newMeasureNo && el.body[0].value < oldMeasureNo && (el.id !== updated.id);
        }
        return false;
      }
    ).map((e):any=> {
      if(e.body[0].value !== undefined) {
        e.body[0].value = Number(e.body[0].value) + 1;
      }
      return e;
    })
  }

  updateFollowingMeasures(deletedMeasureNr:number, delta:number): void {
    this.annotationsData.filter(
      (el) => {
        if (el.body[0].value !== undefined) {
          return el.body[0].value > deletedMeasureNr;
        }
        return false;
      }
    ).map((e):any=> {
      if(e.body[0] !== undefined) {
        e.body[0].value = Number(e.body[0].value) + delta;
      }
      return e;
    })
  }

  previousPage(): void
  {
    if(this.currentPage > 1) {
      this.viewer.open({
        type: 'image',
        url: this.generateUrl(this.storageService.getActiveEditionId(), --this.currentPage -1)
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
    this.editionService.fetchMeasureBoxes(this.storageService.getActiveEditionId(), this.currentPage - 1)
      .subscribe(response => {
          this.editionService.fetchBoxOffset(this.storageService.getActiveEditionId(), this.currentPage - 1)
            .subscribe((res) => {
              if(response.body) {
                if(typeof(res) === "number") {
                  this.offset = res;
                }
                this.annotationsData = this.makeW3CConform(response.body)
                this.currentMeasureNo = this.annotationsData.length + 1;
                this.newMeasureValue = this.currentMeasureNo + this.offset;
                this.initializeAnnotorious()
              }
            });
      });
  }
  save(): void {
    if(this.status === Status.MODIFIED) {
      const measureBoxListConvertedBack = this.convertBack();
      this.editionService.save(this.storageService.getActiveEditionId(), this.currentPage - 1, measureBoxListConvertedBack)
        .subscribe( () =>{
          this.status = Status.SAVED
        });
    }
  }

  changePage(): void {
    if(this.requestedPage !== undefined) {
      this.currentPage = this.requestedPage;
      this.viewer.open({
        type: 'image',
        url: this.generateUrl(this.storageService.getActiveEditionId(), this.currentPage - 1)
      })
      this.setAnnotationsWithServerData();
    }
  }

  changeMeasureNo(): void {
    this.currentMeasureNo = this.newMeasureValue - this.offset;
}

  numberOnly(event:any): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }

  private convertBack(): ApiOrchMeasureBox[] {
    const measureBoxes = []
    for (const annotation of this.annotationsData)  {
      const restoredValues = this.restoreValues(annotation);
      measureBoxes.push(
        new ApiOrchMeasureBoxImpl(
          undefined,
          restoredValues[0], // ulx
          restoredValues[2] + restoredValues[0], // lry
          restoredValues[3] + restoredValues[1], // lrx
          restoredValues[1], // uly
          annotation.body[0].value - this.offset - 1,
          "Edited"
        )
      );
    }
    return measureBoxes;
  }

  private restoreValues(annotation:any): number[] {
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
      measureCount = mb.measureCount + this.offset + 1;
    }
    return {
      "@context": "http:/www.w3.org/ns/anno.jsonId",
      "id": String(mb.id),
      "type": "Annotation",
      "body":  [{
        "type": "TextualBody",
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
    }
  }

//   protected boxComparator(self: ApiOrchMeasureBox, other: ApiOrchMeasureBox): number {

   /* if self['left'] >= other['left'] and self['top'] >= other['top']:
    return +1;

    elif self['left'] < other['left'] and self['top'] < other['top']:
    return -1  # other after self
  else:
    overlap_y = min(self['bottom'] - other['top'], other['bottom'] - self['top']) \
                    / min(self['bottom'] - self['top'], other['bottom'] - other['top'])
    if overlap_y >= 0.5:
    if self['left'] < other['left']:
    return -1
  else:
    return 1
  else:
    if self['left'] < other['left']:
    return 1
  else:
    return -1 */
}


