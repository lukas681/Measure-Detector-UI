import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';


import { ApplicationConfigService } from 'app/core/config/application-config.service';

// export type EntityResponseType = HttpResponse<IEdition>;
// export type EntityArrayResponseType = HttpResponse<IEdition[]>;

@Injectable({ providedIn: 'root' })
export class EditionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/editions');
  protected resourceUrlEditionsByProject = this.applicationConfigService.getEndpointFor('api/editionsByProject');
  protected resourceUrlAddEditionWithFile = this.applicationConfigService.getEndpointFor('api/edition/add');
  protected resourceUrlTriggerDetection = this.applicationConfigService.getEndpointFor('api/edition/runMeasureDetection');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  // triggerMeasureDetection(id: number): Observable<string> {
  // }
}
