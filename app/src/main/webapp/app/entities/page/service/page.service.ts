import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPage, getPageIdentifier } from '../page.model';

export type EntityResponseType = HttpResponse<IPage>;
export type EntityArrayResponseType = HttpResponse<IPage[]>;

@Injectable({ providedIn: 'root' })
export class PageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/pages');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(page: IPage): Observable<EntityResponseType> {
    return this.http.post<IPage>(this.resourceUrl, page, { observe: 'response' });
  }

  update(page: IPage): Observable<EntityResponseType> {
    return this.http.put<IPage>(`${this.resourceUrl}/${getPageIdentifier(page) as number}`, page, { observe: 'response' });
  }

  partialUpdate(page: IPage): Observable<EntityResponseType> {
    return this.http.patch<IPage>(`${this.resourceUrl}/${getPageIdentifier(page) as number}`, page, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPageToCollectionIfMissing(pageCollection: IPage[], ...pagesToCheck: (IPage | null | undefined)[]): IPage[] {
    const pages: IPage[] = pagesToCheck.filter(isPresent);
    if (pages.length > 0) {
      const pageCollectionIdentifiers = pageCollection.map(pageItem => getPageIdentifier(pageItem)!);
      const pagesToAdd = pages.filter(pageItem => {
        const pageIdentifier = getPageIdentifier(pageItem);
        if (pageIdentifier == null || pageCollectionIdentifiers.includes(pageIdentifier)) {
          return false;
        }
        pageCollectionIdentifiers.push(pageIdentifier);
        return true;
      });
      return [...pagesToAdd, ...pageCollection];
    }
    return pageCollection;
  }
}
