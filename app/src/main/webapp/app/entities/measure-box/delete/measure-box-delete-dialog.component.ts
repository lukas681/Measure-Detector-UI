import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMeasureBox } from '../measure-box.model';
import { MeasureBoxService } from '../service/measure-box.service';

@Component({
  templateUrl: './measure-box-delete-dialog.component.html',
})
export class MeasureBoxDeleteDialogComponent {
  measureBox?: IMeasureBox;

  constructor(protected measureBoxService: MeasureBoxService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.measureBoxService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
