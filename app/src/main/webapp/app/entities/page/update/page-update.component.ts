import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPage, Page } from '../page.model';
import { PageService } from '../service/page.service';
import { IEdition } from 'app/entities/edition/edition.model';
import { EditionService } from 'app/entities/edition/service/edition.service';

@Component({
  selector: 'jhi-page-update',
  templateUrl: './page-update.component.html',
})
export class PageUpdateComponent implements OnInit {
  isSaving = false;

  editionsSharedCollection: IEdition[] = [];

  editForm = this.fb.group({
    id: [],
    pageNr: [null, [Validators.required]],
    imgFileReference: [],
    nextPage: [],
    edition: [],
  });

  constructor(
    protected pageService: PageService,
    protected editionService: EditionService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ page }) => {
      this.updateForm(page);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const page = this.createFromForm();
    if (page.id !== undefined) {
      this.subscribeToSaveResponse(this.pageService.update(page));
    } else {
      this.subscribeToSaveResponse(this.pageService.create(page));
    }
  }

  trackEditionById(index: number, item: IEdition): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPage>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(page: IPage): void {
    this.editForm.patchValue({
      id: page.id,
      pageNr: page.pageNr,
      imgFileReference: page.imgFileReference,
      nextPage: page.nextPage,
      edition: page.edition,
    });

    this.editionsSharedCollection = this.editionService.addEditionToCollectionIfMissing(this.editionsSharedCollection, page.edition);
  }

  protected loadRelationshipsOptions(): void {
    this.editionService
      .query()
      .pipe(map((res: HttpResponse<IEdition[]>) => res.body ?? []))
      .pipe(
        map((editions: IEdition[]) => this.editionService.addEditionToCollectionIfMissing(editions, this.editForm.get('edition')!.value))
      )
      .subscribe((editions: IEdition[]) => (this.editionsSharedCollection = editions));
  }

  protected createFromForm(): IPage {
    return {
      ...new Page(),
      id: this.editForm.get(['id'])!.value,
      pageNr: this.editForm.get(['pageNr'])!.value,
      imgFileReference: this.editForm.get(['imgFileReference'])!.value,
      nextPage: this.editForm.get(['nextPage'])!.value,
      edition: this.editForm.get(['edition'])!.value,
    };
  }
}
