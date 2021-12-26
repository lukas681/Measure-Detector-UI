import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';

import { IEdition } from '../editing.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EditionService } from '../service/edition.service';
import { ParseLinks } from 'app/core/util/parse-links.service';

// import '@recogito/annotorious/dist/annotorious.min.css';
import * as OpenSeadragon from 'openseadragon';

import * as OSDAnnotorious from '@recogito/annotorious-openseadragon';
import ShapeLabelsFormatter from '@recogito/annotorious-shape-labels';
// import { Annotorious } from '@recogito/annotorious';

@Component({
  selector: 'jhi-edition',
  templateUrl: './editing.component.html',
  // styleUrls: ['@recogito/annotorious/dist/annotorious.min.css']
})
export class EditingComponent implements OnInit {
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;
  annotationsData = {};
  currentMeasureNo = 1;

  constructor(protected activatedRoute: ActivatedRoute, protected editionService: EditionService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {

    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }


  ngOnInit(): void {
    // const anno = new Annotorious({ image: 'test' }); // image element or ID

      const viewer = OpenSeadragon({
      id: "test",
      prefixUrl: "openseadragon/images/",
      minZoomLevel: 	1,
      maxZoomLevel: 	10,
      // visibilityRatio: 0.1,
      // constrainDuringPan: true,
      tileSources: {
        type: "image",
        url: 'https://www.bsb-muenchen.de/fileadmin/bsb/sammlungen/musik/aktuelles/strauss_richard_metamorphosen_ausschnitt.jpg'
      }
    });
      console.warn(ShapeLabelsFormatter);
    const anno = OSDAnnotorious(viewer, {
      formatter: ShapeLabelsFormatter(),
      // disableEditor: true
    });
    // console.warn(anno)
    this.annotationsData = anno.getAnnotations()

    // TODO: Is there any option to enable two way bindings?
    anno.on('createSelection', async (selection:any) => {
      this.annotationsData = anno.getAnnotations();
      selection.body = [{
        type: 'TextualBody',
        purpose: 'tagging',
        value: this.currentMeasureNo++
      }];

      // console.warn(this.annotationsData);
      await anno.updateSelected(selection);
      anno.saveSelected();
    });

    anno.on('updateAnnotation', () => {
      this.annotationsData = anno.getAnnotations();
    });

    anno.on('deleteAnnotation', () => {
      this.annotationsData = anno.getAnnotations();
      // this.currentMeasureNo--; // Maybe this makes sense, but deleting something in between might be unlogic
    });

    // const anno = new Annotorious ({
    //   image: document.getElementById('test')
    // });
    // anno.loadAnnotations('annotations.w3c.json');

    // Attach listeners to handle annotation events
    // anno.on('createAnnotation', function(annotation: any):any {
    //   const annotations = anno.getAnnotations();
    //   console.warn(annotations);
    // });

    // console.warn(anno)
    console.warn("NONEMPTY")
    // this.activatedRoute.data.subscribe(({ editions}) => {
    //   this.editions = editions;
    //   // eslint-disable-next-line no-console
    //   console.log(this.editions)
    // });

    // this.loadAll();
  }

  trackId(index: number, item: IEdition): number {
    return item.id!;
  }
}

