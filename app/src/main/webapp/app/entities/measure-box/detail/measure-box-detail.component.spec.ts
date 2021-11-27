import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MeasureBoxDetailComponent } from './measure-box-detail.component';

describe('MeasureBox Management Detail Component', () => {
  let comp: MeasureBoxDetailComponent;
  let fixture: ComponentFixture<MeasureBoxDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MeasureBoxDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ measureBox: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MeasureBoxDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MeasureBoxDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load measureBox on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.measureBox).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
