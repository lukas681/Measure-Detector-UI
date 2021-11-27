import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMeasureBox, MeasureBox } from '../measure-box.model';
import { MeasureBoxService } from '../service/measure-box.service';
import { IPage } from 'app/entities/page/page.model';
import { PageService } from 'app/entities/page/service/page.service';

@Component({
  selector: 'jhi-measure-box-update',
  templateUrl: './measure-box-update.component.html',
})
export class MeasureBoxUpdateComponent implements OnInit {
  isSaving = false;

  pagesSharedCollection: IPage[] = [];

  editForm = this.fb.group({
    id: [],
    ulx: [],
    uly: [],
    lrx: [],
    lry: [],
    measureCount: [],
    comment: [],
    page: [],
  });

  constructor(
    protected measureBoxService: MeasureBoxService,
    protected pageService: PageService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ measureBox }) => {
      this.updateForm(measureBox);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const measureBox = this.createFromForm();
    if (measureBox.id !== undefined) {
      this.subscribeToSaveResponse(this.measureBoxService.update(measureBox));
    } else {
      this.subscribeToSaveResponse(this.measureBoxService.create(measureBox));
    }
  }

  trackPageById(index: number, item: IPage): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMeasureBox>>): void {
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

  protected updateForm(measureBox: IMeasureBox): void {
    this.editForm.patchValue({
      id: measureBox.id,
      ulx: measureBox.ulx,
      uly: measureBox.uly,
      lrx: measureBox.lrx,
      lry: measureBox.lry,
      measureCount: measureBox.measureCount,
      comment: measureBox.comment,
      page: measureBox.page,
    });

    this.pagesSharedCollection = this.pageService.addPageToCollectionIfMissing(this.pagesSharedCollection, measureBox.page);
  }

  protected loadRelationshipsOptions(): void {
    this.pageService
      .query()
      .pipe(map((res: HttpResponse<IPage[]>) => res.body ?? []))
      .pipe(map((pages: IPage[]) => this.pageService.addPageToCollectionIfMissing(pages, this.editForm.get('page')!.value)))
      .subscribe((pages: IPage[]) => (this.pagesSharedCollection = pages));
  }

  protected createFromForm(): IMeasureBox {
    return {
      ...new MeasureBox(),
      id: this.editForm.get(['id'])!.value,
      ulx: this.editForm.get(['ulx'])!.value,
      uly: this.editForm.get(['uly'])!.value,
      lrx: this.editForm.get(['lrx'])!.value,
      lry: this.editForm.get(['lry'])!.value,
      measureCount: this.editForm.get(['measureCount'])!.value,
      comment: this.editForm.get(['comment'])!.value,
      page: this.editForm.get(['page'])!.value,
    };
  }
}
