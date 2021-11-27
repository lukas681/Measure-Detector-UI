jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IMeasureBox, MeasureBox } from '../measure-box.model';
import { MeasureBoxService } from '../service/measure-box.service';

import { MeasureBoxRoutingResolveService } from './measure-box-routing-resolve.service';

describe('MeasureBox routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: MeasureBoxRoutingResolveService;
  let service: MeasureBoxService;
  let resultMeasureBox: IMeasureBox | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(MeasureBoxRoutingResolveService);
    service = TestBed.inject(MeasureBoxService);
    resultMeasureBox = undefined;
  });

  describe('resolve', () => {
    it('should return IMeasureBox returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMeasureBox = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultMeasureBox).toEqual({ id: 123 });
    });

    it('should return new IMeasureBox if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMeasureBox = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultMeasureBox).toEqual(new MeasureBox());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as MeasureBox })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMeasureBox = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultMeasureBox).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
