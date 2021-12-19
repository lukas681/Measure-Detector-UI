jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ProjectService } from '../service/project.service';
import { IProject, Project } from '../project.model';

import { ProjectUpdateComponent } from './project-update.component';

describe('Project Management Update Component', () => {
  let comp: ProjectUpdateComponent;
  let fixture: ComponentFixture<ProjectUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let projectService: ProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ProjectUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(ProjectUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProjectUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    projectService = TestBed.inject(ProjectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const project: IProject = { id: 456 };

      activatedRoute.data = of({ project });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(project));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Project>>();
      const project = { id: 123 };
      jest.spyOn(projectService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ project });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: project }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(projectService.update).toHaveBeenCalledWith(project);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Project>>();
      const project = new Project();
      jest.spyOn(projectService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ project });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: project }));
      saveSubject.complete();

      // THEN
      expect(projectService.create).toHaveBeenCalledWith(project);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Project>>();
      const project = { id: 123 };
      jest.spyOn(projectService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ project });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(projectService.update).toHaveBeenCalledWith(project);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
