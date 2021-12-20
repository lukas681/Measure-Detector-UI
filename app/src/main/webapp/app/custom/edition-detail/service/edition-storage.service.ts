import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class StorageService {

  public activeProjectId: number | boolean = false;


  public getActiveProjectId(): number | boolean {

    // eslint-disable-next-line no-console
    console.log("called getActiveProfileID");

    // eslint-disable-next-line no-console
    console.log(this.activeProjectId);
    return this.activeProjectId;
  }

  public setActiveProjectID(projectId: number): void {

    // eslint-disable-next-line no-console
    console.log("called setActiveProfileID");
    this.activeProjectId = projectId;
  }
}
