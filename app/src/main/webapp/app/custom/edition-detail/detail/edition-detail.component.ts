import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {StorageService} from '../service/edition-storage.service'
import { IEdition } from '../edition-detail.model';

@Component({
  selector: 'jhi-edition-detail',
  templateUrl: './edition-detail.component.html',
})
export class EditionDetailComponent implements OnInit {
  edition: IEdition | null = null;


  constructor(protected storageService: StorageService, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ edition }) => {
      this.edition = edition;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
