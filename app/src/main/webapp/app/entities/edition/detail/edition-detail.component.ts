import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEdition } from '../edition.model';

@Component({
  selector: 'jhi-edition-detail',
  templateUrl: './edition-detail.component.html',
})
export class EditionDetailComponent implements OnInit {
  edition: IEdition | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ edition }) => {
      this.edition = edition;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
