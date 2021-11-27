import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MeasureBoxComponent } from '../list/measure-box.component';
import { MeasureBoxDetailComponent } from '../detail/measure-box-detail.component';
import { MeasureBoxUpdateComponent } from '../update/measure-box-update.component';
import { MeasureBoxRoutingResolveService } from './measure-box-routing-resolve.service';

const measureBoxRoute: Routes = [
  {
    path: '',
    component: MeasureBoxComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MeasureBoxDetailComponent,
    resolve: {
      measureBox: MeasureBoxRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MeasureBoxUpdateComponent,
    resolve: {
      measureBox: MeasureBoxRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MeasureBoxUpdateComponent,
    resolve: {
      measureBox: MeasureBoxRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(measureBoxRoute)],
  exports: [RouterModule],
})
export class MeasureBoxRoutingModule {}
