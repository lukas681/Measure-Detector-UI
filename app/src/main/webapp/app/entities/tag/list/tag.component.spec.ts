import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TagService } from '../service/tag.service';

import { TagComponent } from './tag.component';

describe('Tag Management Component', () => {
  let comp: TagComponent;
  let fixture: ComponentFixture<TagComponent>;
  let service: TagService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TagComponent],
    })
      .overrideTemplate(TagComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TagComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TagService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.tags?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
