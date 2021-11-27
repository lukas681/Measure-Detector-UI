import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PageComponent } from '../list/page.component';
import { PageDetailComponent } from '../detail/page-detail.component';
import { PageUpdateComponent } from '../update/page-update.component';
import { PageRoutingResolveService } from './page-routing-resolve.service';

const pageRoute: Routes = [
  {
    path: '',
    component: PageComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PageDetailComponent,
    resolve: {
      page: PageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PageUpdateComponent,
    resolve: {
      page: PageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PageUpdateComponent,
    resolve: {
      page: PageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(pageRoute)],
  exports: [RouterModule],
})
export class PageRoutingModule {}
