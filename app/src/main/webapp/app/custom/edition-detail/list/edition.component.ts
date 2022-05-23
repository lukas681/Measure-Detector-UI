import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse, HttpEventType } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { saveAs } from 'file-saver'
import { tap, last } from 'rxjs/operators'

import { IEdition } from '../edition-detail.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EditionService } from '../service/edition.service';
import { EditionDeleteDialogComponent } from '../delete/edition-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';
import {StorageService} from '../service/edition-storage.service'

@Component({
  selector: 'jhi-edition',
  templateUrl: './edition.component.html',
})
export class EditionComponent implements OnInit {
  editions: IEdition[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected storageService: StorageService, protected activatedRoute: ActivatedRoute, protected editionService: EditionService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.editions = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    // this.isLoading = true;
  //
  //   this.editionService
  //     .query({
  //       id: this.activatedRouter.data.id,
  //       page: this.page,
  //       size: this.itemsPerPage,
  //       sort: this.sort(),
  //     })
  //     .subscribe(
  //       (res: HttpResponse<IEdition[]>) => {
  //         this.isLoading = false;
  //         this.paginateEditions(res.body, res.headers);
  //       },
  //       () => {
  //         this.isLoading = false;
  //       }
  //     );
  }

  reset(): void {
    this.page = 0;
    // this.editions = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    const z = this.storageService.getActiveProjectId()
    // eslint-disable-next-line no-console
    console.log(z);
    this.activatedRoute.data.subscribe(({ editions}) => {
      this.editions = editions;
      // eslint-disable-next-line no-console
      console.log(this.editions)
    });

    // eslint-disable-next-line no-console
    console.log(this.editions)
    // this.loadAll();
  }

  trackId(index: number, item: IEdition): number {
    return item.id!;
  }

  delete(edition: IEdition): void {
    const modalRef = this.modalService.open(EditionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.edition = edition;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  triggerDetection(id: number | undefined): void {
    if(id){
      this.editionService.triggerMeasureDetection(id)
        .subscribe(x=> {
          console.warn("Finished")
        });
    }
  }

  downloadAnnotatedPDF(edition: IEdition): void {
    if(edition.id) {
      this.isLoading=true;
      this.editionService.downloadPDF(edition.id)
        .subscribe(
          blob => {
            this.isLoading = false;
            saveAs(blob, String(edition.pDFFileName))
          },
          err => {
            // An error occured
            this.isLoading = false;
          }

        );
    }
  }

  previousState(): void {
    window.history.back();
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateEditions(data: IEdition[] | null, headers: HttpHeaders): void {
    const linkHeader = headers.get('link');
    if (linkHeader) {
      this.links = this.parseLinks.parse(linkHeader);
    } else {
      this.links = {
        last: 0,
      };
    }
    if (data) {
      for (const d of data) {
        this.editions.push(d);
      }
    }
  }

}
