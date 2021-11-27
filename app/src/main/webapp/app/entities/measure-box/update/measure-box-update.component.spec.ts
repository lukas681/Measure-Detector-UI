jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { MeasureBoxService } from '../service/measure-box.service';
import { IMeasureBox, MeasureBox } from '../measure-box.model';
import { IPage } from 'app/entities/page/page.model';
import { PageService } from 'app/entities/page/service/page.service';

import { MeasureBoxUpdateComponent } from './measure-box-update.component';

describe('MeasureBox Management Update Component', () => {
  let comp: MeasureBoxUpdateComponent;
  let fixture: ComponentFixture<MeasureBoxUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let measureBoxService: MeasureBoxService;
  let pageService: PageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [MeasureBoxUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(MeasureBoxUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MeasureBoxUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    measureBoxService = TestBed.inject(MeasureBoxService);
    pageService = TestBed.inject(PageService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Page query and add missing value', () => {
      const measureBox: IMeasureBox = { id: 456 };
      const page: IPage = { id: 17995 };
      measureBox.page = page;

      const pageCollection: IPage[] = [{ id: 76812 }];
      jest.spyOn(pageService, 'query').mockReturnValue(of(new HttpResponse({ body: pageCollection })));
      const additionalPages = [page];
      const expectedCollection: IPage[] = [...additionalPages, ...pageCollection];
      jest.spyOn(pageService, 'addPageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ measureBox });
      comp.ngOnInit();

      expect(pageService.query).toHaveBeenCalled();
      expect(pageService.addPageToCollectionIfMissing).toHaveBeenCalledWith(pageCollection, ...additionalPages);
      expect(comp.pagesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const measureBox: IMeasureBox = { id: 456 };
      const page: IPage = { id: 130 };
      measureBox.page = page;

      activatedRoute.data = of({ measureBox });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(measureBox));
      expect(comp.pagesSharedCollection).toContain(page);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<MeasureBox>>();
      const measureBox = { id: 123 };
      jest.spyOn(measureBoxService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ measureBox });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: measureBox }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(measureBoxService.update).toHaveBeenCalledWith(measureBox);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<MeasureBox>>();
      const measureBox = new MeasureBox();
      jest.spyOn(measureBoxService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ measureBox });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: measureBox }));
      saveSubject.complete();

      // THEN
      expect(measureBoxService.create).toHaveBeenCalledWith(measureBox);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<MeasureBox>>();
      const measureBox = { id: 123 };
      jest.spyOn(measureBoxService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ measureBox });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(measureBoxService.update).toHaveBeenCalledWith(measureBox);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPageById', () => {
      it('Should return tracked Page primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPageById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
