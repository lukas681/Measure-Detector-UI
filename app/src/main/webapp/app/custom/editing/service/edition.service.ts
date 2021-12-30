import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';


import { ApplicationConfigService } from 'app/core/config/application-config.service';
import {IMeasureBox} from "../../../entities/measure-box/measure-box.model";
import {EntityResponseType} from "../../../entities/measure-box/service/measure-box.service";
import {ApiOrchMeasureBox} from "../../../shared/model/openapi/model/apiOrchMeasureBox";
import {IProject} from "../../myprojects/project.model";

// export type EntityResponseType = HttpResponse<IEdition>;
// export type EntityArrayResponseType = HttpResponse<IEdition[]>;

export type EntityArrayResponseType = HttpResponse<ApiOrchMeasureBox[]>;

@Injectable({ providedIn: 'root' })
export class EditionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/editions');
  protected resourceUrlEditionsByProject = this.applicationConfigService.getEndpointFor('api/editionsByProject');
  protected resourceUrlAddEditionWithFile = this.applicationConfigService.getEndpointFor('api/edition/add');
  protected resourceUrlTriggerDetection = this.applicationConfigService.getEndpointFor('api/edition/runMeasureDetection');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  fetchMeasureBoxes(editionId: number | boolean| undefined, pageNr: number): Observable<EntityArrayResponseType> {
    const url = '/api/edition/' + String(editionId) + "/getMeasureBoxes/" + String(pageNr);
    return this.http.get<ApiOrchMeasureBox[]>(url, { observe: 'response' });
  }

  // triggerMeasureDetection(id: number): Observable<string> {
  // }
}
