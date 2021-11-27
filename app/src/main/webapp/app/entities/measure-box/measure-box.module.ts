import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MeasureBoxComponent } from './list/measure-box.component';
import { MeasureBoxDetailComponent } from './detail/measure-box-detail.component';
import { MeasureBoxUpdateComponent } from './update/measure-box-update.component';
import { MeasureBoxDeleteDialogComponent } from './delete/measure-box-delete-dialog.component';
import { MeasureBoxRoutingModule } from './route/measure-box-routing.module';

@NgModule({
  imports: [SharedModule, MeasureBoxRoutingModule],
  declarations: [MeasureBoxComponent, MeasureBoxDetailComponent, MeasureBoxUpdateComponent, MeasureBoxDeleteDialogComponent],
  entryComponents: [MeasureBoxDeleteDialogComponent],
})
export class MeasureBoxModule {}
