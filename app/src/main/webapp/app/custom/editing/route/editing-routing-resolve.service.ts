import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import {StorageService} from "../../edition-detail/service/edition-storage.service";

import { EditionService } from '../service/edition.service';

@Injectable({ providedIn: 'root' })
export class EditingRoutingResolveService implements Resolve<string> {
  constructor(protected storageService: StorageService, protected service: EditionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<string> | Observable<never> {

    const id = route.params['id'];
    this.storageService.setActiveEditionId(id);

    return of("");
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