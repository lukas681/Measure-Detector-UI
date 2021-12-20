import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEdition, Edition } from '../edition-detail.model';
import { EditionService } from '../service/edition.service';
import {StorageService} from '../service/edition-storage.service'

@Injectable({ providedIn: 'root' })
export class FindNecessaryEditionsService implements Resolve<IEdition[]> {

  constructor(private storageService: StorageService, protected service: EditionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEdition[]> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.findByProjectId(id).pipe(
        mergeMap((edition: HttpResponse<Edition[]>) => {
          this.storageService.setActiveProjectID(id);
          if (edition.body) {
            return of(edition.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    const res: IEdition[] = [];
    res.push(new Edition());
    return of(res);
  }
}
