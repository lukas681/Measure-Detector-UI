import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMeasureBox } from '../measure-box.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { MeasureBoxService } from '../service/measure-box.service';
import { MeasureBoxDeleteDialogComponent } from '../delete/measure-box-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-measure-box',
  templateUrl: './measure-box.component.html',
})
export class MeasureBoxComponent implements OnInit {
  measureBoxes: IMeasureBox[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected measureBoxService: MeasureBoxService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.measureBoxes = [];
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

    this.measureBoxService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IMeasureBox[]>) => {
          this.isLoading = false;
          this.paginateMeasureBoxes(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.measureBoxes = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IMeasureBox): number {
    return item.id!;
  }

  delete(measureBox: IMeasureBox): void {
    const modalRef = this.modalService.open(MeasureBoxDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.measureBox = measureBox;
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

  protected paginateMeasureBoxes(data: IMeasureBox[] | null, headers: HttpHeaders): void {
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
        this.measureBoxes.push(d);
      }
    }
  }
}
