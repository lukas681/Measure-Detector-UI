import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';


import { ApplicationConfigService } from 'app/core/config/application-config.service';
import {ApiOrchMeasureBox} from "../../../shared/model/openapi/model/apiOrchMeasureBox";

export type EntityArrayResponseType = HttpResponse<ApiOrchMeasureBox[]>;
export type simpleLongReturn = HttpResponse<number>;

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
  fetchBoxOffset(editionId: number | boolean| undefined, pageNr: number): Observable<simpleLongReturn> {
    const url = '/api/edition/' + String(editionId) + "/getMeasureBoxesOffset/" + String(pageNr);
    return this.http.get<simpleLongReturn>(url);
  }
  fetchTotalPageCount(editionId: number | boolean| undefined): Observable<simpleLongReturn> {
    const url = '/api/edition/' + String(editionId) + "/getNumberPages";
    return this.http.get<simpleLongReturn>(url);
  }
  save(editionId: number | boolean| undefined, pageNr: number, boxes: ApiOrchMeasureBox[]): Observable<string> {
    const url = '/api/edition/' + String(editionId) + "/saveMeasureBoxes/" + String(pageNr);
    return this.http.post<string>(url, boxes);
  }
}
