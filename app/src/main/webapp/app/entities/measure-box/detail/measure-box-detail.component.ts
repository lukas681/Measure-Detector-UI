import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMeasureBox } from '../measure-box.model';

@Component({
  selector: 'jhi-measure-box-detail',
  templateUrl: './measure-box-detail.component.html',
})
export class MeasureBoxDetailComponent implements OnInit {
  measureBox: IMeasureBox | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ measureBox }) => {
      this.measureBox = measureBox;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
