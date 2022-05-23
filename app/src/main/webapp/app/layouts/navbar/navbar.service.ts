import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import {ApiOrchJobStats} from "../../shared/model/openapi/model/ApiOrchJobStats";
import {EntityResponseType} from "../../entities/tag/service/tag.service";

@Injectable({ providedIn: 'root' })
export class NavbarService {
  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  public getJobStats(): Observable<HttpResponse<ApiOrchJobStats>> {
    return this.http.get(this.applicationConfigService.getEndpointFor('api/system/getJobStats'),
      {
      observe: 'response'
    });
  }
}
