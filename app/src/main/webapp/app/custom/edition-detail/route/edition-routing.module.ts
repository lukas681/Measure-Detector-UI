import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EditionComponent } from '../list/edition.component';
import { EditionDetailComponent } from '../detail/edition-detail.component';
import { EditionUpdateComponent } from '../update/edition-update.component';
import { EditionRoutingResolveService } from './edition-routing-resolve.service';
import {FindNecessaryEditionsService} from "./find-necessary-editions.service";

const editionRoute: Routes = [
  {
    path: ':id/',
    component: EditionComponent,
    resolve: {
      editions: FindNecessaryEditionsService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EditionDetailComponent,
    resolve: {
      edition: EditionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EditionUpdateComponent,
    resolve: {
      edition: EditionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EditionUpdateComponent,
    resolve: {
      edition: EditionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(editionRoute)],
  exports: [RouterModule],
})
export class EditionRoutingModule {}
