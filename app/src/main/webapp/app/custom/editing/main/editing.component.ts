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

  BASEURL = "/api/";
  // TODO do not allow out of boundary calls
  currentPage = 0;
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
      minZoomLevel: 	1,
      maxZoomLevel: 	13,
      tileSources:
        {
          type: 'image',
          url: this.generateUrl(this.storageService.getActiveEditionId(), ++this.currentPage)
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
    // console.warn(this.generateUrl( this.storageService.getActiveEditionId(), this.currentPage));
    this.anno.clearAnnotations();
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

  }
  previousPage(): void
  {
    this.anno.clearAnnotations();
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


}

