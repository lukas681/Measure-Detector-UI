import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEdition, Edition } from '../edition-detail.model';
import { EditionService } from '../service/edition.service';

@Injectable({ providedIn: 'root' })
export class EditionRoutingResolveService implements Resolve<IEdition> {
  constructor(protected service: EditionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEdition> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((edition: HttpResponse<Edition>) => {
          if (edition.body) {
            return of(edition.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Edition());
  }
}
