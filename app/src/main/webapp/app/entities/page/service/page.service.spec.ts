import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPage, Page } from '../page.model';

import { PageService } from './page.service';

describe('Page Service', () => {
  let service: PageService;
  let httpMock: HttpTestingController;
  let elemDefault: IPage;
  let expectedResult: IPage | IPage[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PageService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      pageNr: 0,
      imgFileReference: 'AAAAAAA',
      nextPage: 0,
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

    it('should create a Page', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Page()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Page', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          pageNr: 1,
          imgFileReference: 'BBBBBB',
          nextPage: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Page', () => {
      const patchObject = Object.assign(
        {
          pageNr: 1,
          imgFileReference: 'BBBBBB',
        },
        new Page()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Page', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          pageNr: 1,
          imgFileReference: 'BBBBBB',
          nextPage: 1,
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

    it('should delete a Page', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPageToCollectionIfMissing', () => {
      it('should add a Page to an empty array', () => {
        const page: IPage = { id: 123 };
        expectedResult = service.addPageToCollectionIfMissing([], page);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(page);
      });

      it('should not add a Page to an array that contains it', () => {
        const page: IPage = { id: 123 };
        const pageCollection: IPage[] = [
          {
            ...page,
          },
          { id: 456 },
        ];
        expectedResult = service.addPageToCollectionIfMissing(pageCollection, page);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Page to an array that doesn't contain it", () => {
        const page: IPage = { id: 123 };
        const pageCollection: IPage[] = [{ id: 456 }];
        expectedResult = service.addPageToCollectionIfMissing(pageCollection, page);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(page);
      });

      it('should add only unique Page to an array', () => {
        const pageArray: IPage[] = [{ id: 123 }, { id: 456 }, { id: 2794 }];
        const pageCollection: IPage[] = [{ id: 123 }];
        expectedResult = service.addPageToCollectionIfMissing(pageCollection, ...pageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const page: IPage = { id: 123 };
        const page2: IPage = { id: 456 };
        expectedResult = service.addPageToCollectionIfMissing([], page, page2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(page);
        expect(expectedResult).toContain(page2);
      });

      it('should accept null and undefined values', () => {
        const page: IPage = { id: 123 };
        expectedResult = service.addPageToCollectionIfMissing([], null, page, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(page);
      });

      it('should return initial array if no Page is added', () => {
        const pageCollection: IPage[] = [{ id: 123 }];
        expectedResult = service.addPageToCollectionIfMissing(pageCollection, undefined, null);
        expect(expectedResult).toEqual(pageCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
