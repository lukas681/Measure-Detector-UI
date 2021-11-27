import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMeasureBox, MeasureBox } from '../measure-box.model';
import { MeasureBoxService } from '../service/measure-box.service';

@Injectable({ providedIn: 'root' })
export class MeasureBoxRoutingResolveService implements Resolve<IMeasureBox> {
  constructor(protected service: MeasureBoxService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMeasureBox> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((measureBox: HttpResponse<MeasureBox>) => {
          if (measureBox.body) {
            return of(measureBox.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new MeasureBox());
  }
}
