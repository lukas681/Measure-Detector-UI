import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPage, Page } from '../page.model';
import { PageService } from '../service/page.service';

@Injectable({ providedIn: 'root' })
export class PageRoutingResolveService implements Resolve<IPage> {
  constructor(protected service: PageService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPage> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((page: HttpResponse<Page>) => {
          if (page.body) {
            return of(page.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Page());
  }
}
