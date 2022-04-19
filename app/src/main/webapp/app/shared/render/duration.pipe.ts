import { Pipe, PipeTransform } from '@angular/core';


@Pipe({
  name: 'add',
})
export class AddPipe implements PipeTransform {
  transform(valueA: number, valueB: number): number {
    return valueA + valueB;
  }
}
