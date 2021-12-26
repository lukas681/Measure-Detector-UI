import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EditingComponent } from '../main/editing.component';
import { EditingRoutingResolveService } from './editing-routing-resolve.service';

const editingRoute: Routes = [
  {
    path: '',
    component: EditingComponent,
    resolve: {
      edition: EditingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(editingRoute)],
  exports: [RouterModule],
})
export class EditingRoutingModule {}
