import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProject, getProjectIdentifier } from '../project.model';

export type EntityResponseType = HttpResponse<IProject>;
export type EntityArrayResponseType = HttpResponse<IProject[]>;

@Injectable({ providedIn: 'root' })
export class ProjectService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/projects');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(project: IProject): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(project);
    return this.http
      .post<IProject>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(project: IProject): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(project);
    return this.http
      .put<IProject>(`${this.resourceUrl}/${getProjectIdentifier(project) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(project: IProject): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(project);
    return this.http
      .patch<IProject>(`${this.resourceUrl}/${getProjectIdentifier(project) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IProject>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IProject[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addProjectToCollectionIfMissing(projectCollection: IProject[], ...projectsToCheck: (IProject | null | undefined)[]): IProject[] {
    const projects: IProject[] = projectsToCheck.filter(isPresent);
    if (projects.length > 0) {
      const projectCollectionIdentifiers = projectCollection.map(projectItem => getProjectIdentifier(projectItem)!);
      const projectsToAdd = projects.filter(projectItem => {
        const projectIdentifier = getProjectIdentifier(projectItem);
        if (projectIdentifier == null || projectCollectionIdentifiers.includes(projectIdentifier)) {
          return false;
        }
        projectCollectionIdentifiers.push(projectIdentifier);
        return true;
      });
      return [...projectsToAdd, ...projectCollection];
    }
    return projectCollection;
  }

  protected convertDateFromClient(project: IProject): IProject {
    return Object.assign({}, project, {
      createdDate: project.createdDate?.isValid() ? project.createdDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((project: IProject) => {
        project.createdDate = project.createdDate ? dayjs(project.createdDate) : undefined;
      });
    }
    return res;
  }
}
