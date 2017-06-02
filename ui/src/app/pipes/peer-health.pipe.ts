import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'peerHealth'
})
export class PeerHealthPipe implements PipeTransform {

  transform(value: number): string {
  	if (value >= 10) {
  		return 'text-success';
  	} else if (value >= 2) {
  		return 'text-warning';
  	} else {
  		return 'text-danger';
  	}
  }

}
