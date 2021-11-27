import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPage } from '../page.model';

@Component({
  selector: 'jhi-page-detail',
  templateUrl: './page-detail.component.html',
})
export class PageDetailComponent implements OnInit {
  page: IPage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ page }) => {
      this.page = page;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
