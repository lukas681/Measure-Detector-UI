import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { EditionType } from 'app/entities/enumerations/edition-type.model';
import { IEdition, Edition } from '../edition.model';

import { EditionService } from './edition.service';

describe('Edition Service', () => {
  let service: EditionService;
  let httpMock: HttpTestingController;
  let elemDefault: IEdition;
  let expectedResult: IEdition | IEdition[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EditionService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      title: 'AAAAAAA',
      createdDate: currentDate,
      type: EditionType.SCORE,
      description: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          createdDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Edition', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          createdDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Edition()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Edition', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          type: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Edition', () => {
      const patchObject = Object.assign(
        {
          title: 'BBBBBB',
        },
        new Edition()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          createdDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Edition', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          type: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Edition', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEditionToCollectionIfMissing', () => {
      it('should add a Edition to an empty array', () => {
        const edition: IEdition = { id: 123 };
        expectedResult = service.addEditionToCollectionIfMissing([], edition);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(edition);
      });

      it('should not add a Edition to an array that contains it', () => {
        const edition: IEdition = { id: 123 };
        const editionCollection: IEdition[] = [
          {
            ...edition,
          },
          { id: 456 },
        ];
        expectedResult = service.addEditionToCollectionIfMissing(editionCollection, edition);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Edition to an array that doesn't contain it", () => {
        const edition: IEdition = { id: 123 };
        const editionCollection: IEdition[] = [{ id: 456 }];
        expectedResult = service.addEditionToCollectionIfMissing(editionCollection, edition);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(edition);
      });

      it('should add only unique Edition to an array', () => {
        const editionArray: IEdition[] = [{ id: 123 }, { id: 456 }, { id: 58873 }];
        const editionCollection: IEdition[] = [{ id: 123 }];
        expectedResult = service.addEditionToCollectionIfMissing(editionCollection, ...editionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const edition: IEdition = { id: 123 };
        const edition2: IEdition = { id: 456 };
        expectedResult = service.addEditionToCollectionIfMissing([], edition, edition2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(edition);
        expect(expectedResult).toContain(edition2);
      });

      it('should accept null and undefined values', () => {
        const edition: IEdition = { id: 123 };
        expectedResult = service.addEditionToCollectionIfMissing([], null, edition, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(edition);
      });

      it('should return initial array if no Edition is added', () => {
        const editionCollection: IEdition[] = [{ id: 123 }];
        expectedResult = service.addEditionToCollectionIfMissing(editionCollection, undefined, null);
        expect(expectedResult).toEqual(editionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
