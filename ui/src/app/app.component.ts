import { Component } from '@angular/core';
import {SearchService} from './search/search.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [SearchService]
})
export class AppComponent {
  title = 'app works!';
}
