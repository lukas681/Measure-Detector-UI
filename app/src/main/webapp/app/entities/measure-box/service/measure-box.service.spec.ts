import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMeasureBox, MeasureBox } from '../measure-box.model';

import { MeasureBoxService } from './measure-box.service';

describe('MeasureBox Service', () => {
  let service: MeasureBoxService;
  let httpMock: HttpTestingController;
  let elemDefault: IMeasureBox;
  let expectedResult: IMeasureBox | IMeasureBox[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MeasureBoxService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      ulx: 0,
      uly: 0,
      lrx: 0,
      lry: 0,
      measureCount: 0,
      comment: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a MeasureBox', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new MeasureBox()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MeasureBox', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          ulx: 1,
          uly: 1,
          lrx: 1,
          lry: 1,
          measureCount: 1,
          comment: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MeasureBox', () => {
      const patchObject = Object.assign(
        {
          ulx: 1,
          lrx: 1,
        },
        new MeasureBox()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MeasureBox', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          ulx: 1,
          uly: 1,
          lrx: 1,
          lry: 1,
          measureCount: 1,
          comment: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a MeasureBox', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMeasureBoxToCollectionIfMissing', () => {
      it('should add a MeasureBox to an empty array', () => {
        const measureBox: IMeasureBox = { id: 123 };
        expectedResult = service.addMeasureBoxToCollectionIfMissing([], measureBox);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(measureBox);
      });

      it('should not add a MeasureBox to an array that contains it', () => {
        const measureBox: IMeasureBox = { id: 123 };
        const measureBoxCollection: IMeasureBox[] = [
          {
            ...measureBox,
          },
          { id: 456 },
        ];
        expectedResult = service.addMeasureBoxToCollectionIfMissing(measureBoxCollection, measureBox);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MeasureBox to an array that doesn't contain it", () => {
        const measureBox: IMeasureBox = { id: 123 };
        const measureBoxCollection: IMeasureBox[] = [{ id: 456 }];
        expectedResult = service.addMeasureBoxToCollectionIfMissing(measureBoxCollection, measureBox);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(measureBox);
      });

      it('should add only unique MeasureBox to an array', () => {
        const measureBoxArray: IMeasureBox[] = [{ id: 123 }, { id: 456 }, { id: 60745 }];
        const measureBoxCollection: IMeasureBox[] = [{ id: 123 }];
        expectedResult = service.addMeasureBoxToCollectionIfMissing(measureBoxCollection, ...measureBoxArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const measureBox: IMeasureBox = { id: 123 };
        const measureBox2: IMeasureBox = { id: 456 };
        expectedResult = service.addMeasureBoxToCollectionIfMissing([], measureBox, measureBox2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(measureBox);
        expect(expectedResult).toContain(measureBox2);
      });

      it('should accept null and undefined values', () => {
        const measureBox: IMeasureBox = { id: 123 };
        expectedResult = service.addMeasureBoxToCollectionIfMissing([], null, measureBox, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(measureBox);
      });

      it('should return initial array if no MeasureBox is added', () => {
        const measureBoxCollection: IMeasureBox[] = [{ id: 123 }];
        expectedResult = service.addMeasureBoxToCollectionIfMissing(measureBoxCollection, undefined, null);
        expect(expectedResult).toEqual(measureBoxCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
