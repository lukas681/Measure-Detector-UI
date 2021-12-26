import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { EditionService } from '../service/edition.service';

@Injectable({ providedIn: 'root' })
export class EditingRoutingResolveService implements Resolve<string> {
  constructor(protected service: EditionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<string> | Observable<never> {
    // const id = route.params['id'];
    // this.storageService.setActiveProjectID(id);

    return of("asd");
    // if (id) {
    //   return this.service.find(id).pipe(
    //     mergeMap((edition: HttpResponse<Edition>) => {
    //       if (edition.body) {
    //         return of(edition.body);
    //       } else {
    //         this.router.navigate(['404']);
    //         return EMPTY;
    //       }
    //     })
    //   );
    // }
    // return of(new Edition());
  }
}
