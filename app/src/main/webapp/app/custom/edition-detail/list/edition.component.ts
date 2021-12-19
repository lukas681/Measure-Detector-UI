import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';

import { IEdition } from '../edition-detail.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EditionService } from '../service/edition.service';
import { EditionDeleteDialogComponent } from '../delete/edition-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

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

  constructor(protected activatedRouter: ActivatedRoute, protected editionService: EditionService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
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
    this.isLoading = true;
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
    this.editions = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
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
