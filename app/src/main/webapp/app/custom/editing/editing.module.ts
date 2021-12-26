import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import {EditingRoutingModule} from "./route/editing-routing.module";
import {EditingComponent} from "./main/editing.component";

@NgModule({
  imports: [SharedModule, EditingRoutingModule, ],
  declarations: [EditingComponent],
  entryComponents: [EditingComponent],
})
export class EditingModule {}
