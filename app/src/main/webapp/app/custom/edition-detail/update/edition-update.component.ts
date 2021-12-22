import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IEdition, Edition } from '../edition-detail.model';
import { EditionService } from '../service/edition.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { EditionType } from 'app/entities/enumerations/edition-type.model';
import {StorageService} from '../service/edition-storage.service'


@Component({
  selector: 'jhi-edition-update',
  templateUrl: './edition-update.component.html',
})
export class EditionUpdateComponent implements OnInit {
  isSaving = false;
  editionTypeValues = Object.keys(EditionType);

  projectsSharedCollection: IProject[] = [];

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required, Validators.pattern('^[A-Z-a-z\\d]+$')]],
    createdDate: [],
    type: [],
    description: [],
    pDFFileName: [],
    projectId: [],
    pdfFile: []
  });

  constructor(
    protected storageService: StorageService,
    protected editionService: EditionService,
    protected projectService: ProjectService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    // If something arrives: Edit Mode
    this.activatedRoute.data.subscribe(({ edition }) => {

      if (edition.id === undefined) {
        // Create.New Mode
        edition.createdDate = dayjs();
        edition.projectId = this.storageService.getActiveProjectId();
      }
      this.updateForm(edition);
      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {

    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const edition = this.createFromForm();
    if (edition.id !== undefined) {
      this.subscribeToSaveResponse(this.editionService.update(edition));
    } else {
      this.subscribeToSaveResponse(this.editionService.create(edition));
    }
  }

  trackProjectById(index: number, item: IProject): number {
    return item.id!;
  }

  selectFile(event: any): void {
    const reader = new FileReader();

    // console.warn(event.target.files[0])

    if(event.target.files) {
      const [file] = event.target.files
      reader.readAsDataURL(file)
      reader.onload = () =>{
        this.editForm.patchValue({
          pdfFile: reader.result,
          pDFFilename: file.filename
        })

      }
      // this.cd.markForCheck
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEdition>>): void {
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


  protected updateForm(edition: IEdition): void {

    this.editForm.patchValue({
      // id: edition.id,
      id: edition.id,
      title: edition.title,
      createdDate: edition.createdDate ? edition.createdDate.format(DATE_TIME_FORMAT) : null,
      type: edition.type,
      description: edition.description,
      pDFFileName: edition.pDFFileName,
      // project: this.storageService.getActiveProjectId(),
      projectId: edition.projectId,
     // TODO Maybe send the PDF file back?
    });
    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing(this.projectsSharedCollection, edition.project);
  }

  protected loadRelationshipsOptions(): void {
    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(
        map((projects: IProject[]) => this.projectService.addProjectToCollectionIfMissing(projects, this.editForm.get('projectId')!.value))
      )
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));
  }

  protected createFromForm(): IEdition {
    return {
      ...new Edition(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      type: this.editForm.get(['type'])!.value,
      description: this.editForm.get(['description'])!.value,
      pDFFileName: this.editForm.get(['pDFFileName'])!.value,
      projectId: this.editForm.get(['projectId'])!.value,
      pdfFile: this.editForm.get(['pdfFile'])!.value,
    };
  }

}
