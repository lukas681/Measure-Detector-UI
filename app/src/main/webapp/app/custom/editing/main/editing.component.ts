import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';

import { IEdition } from '../editing.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EditionService } from '../service/edition.service';
import { ParseLinks } from 'app/core/util/parse-links.service';

import * as OpenSeadragon from 'openseadragon';
import * as OSDAnnotorious from '@recogito/annotorious-openseadragon';
import ShapeLabelsFormatter from '@recogito/annotorious-shape-labels';
import {TileSource} from "openseadragon";
import {StorageService} from "../../edition-detail/service/edition-storage.service";

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
  annotationsData = [];
  currentMeasureNo = 1;
  viewer: any;
  anno: any;

  constructor(protected storageService: StorageService,protected activatedRoute: ActivatedRoute, protected editionService: EditionService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  ngOnInit(): void {

      this.viewer = OpenSeadragon({
      id: "viewer",
      prefixUrl: "openseadragon/images/",
      minZoomLevel: 	1,
      maxZoomLevel: 	13,
          // visibilityRatio: 0.1,
      // constrainDuringPan: true,
      tileSources: {
        type: 'image',
        url: 'https://www.bsb-muenchen.de/fileadmin/bsb/sammlungen/musik/aktuelles/strauss_richard_metamorphosen_ausschnitt.jpg'
      }
    }
    );
      console.warn(ShapeLabelsFormatter);
    this.anno = OSDAnnotorious(this.viewer, {
      formatter: ShapeLabelsFormatter(),
      // disableEditor: true
    });
    this.annotationsData = this.anno.getAnnotations()

    this.anno.on('createSelection', async (selection:any) => {
      this.annotationsData = this.anno.getAnnotations();
      selection.body = [{
        type: 'TextualBody',
        purpose: 'tagging',
        value: this.currentMeasureNo++
      }];

      console.warn(this.annotationsData);
      await this.anno.updateSelected(selection);
      this.anno.saveSelected();
    });

    this.anno.on('updateAnnotation', () => {
      this.annotationsData = this.anno.getAnnotations();
    });

    this.anno.on('deleteAnnotation', () => {
      this.annotationsData = this.anno.getAnnotations();
      // this.currentMeasureNo--; // Maybe this makes sense, but deleting something in between might be unlogic
    });
  }

  nextPage(): void
  {
    // resets all
    this.anno.clearAnnotations();
    //B TODO implement this.
    this.viewer.open({
      type: 'image',
      url: 'http://localhost:12321/api/edition/24/getPage/2'
    })

  }

  trackId(index: number, item: IEdition): number {
    return item.id!;
  }

  /**
   * Takes a Measure Numebr and decreases all following ones by one. Used for deleting and filling the open space
   * @param n
   */
  // decreaseAllFollowedFromN(n: number): void {
  //     this.annotationsData.filter((annotation)=> {
  //       if(annotation.hasOwnProperty("target"))
  //         return true;
  //     });
  // }
}

