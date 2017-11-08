import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import {
  NavbarComponent,
  FooterComponent,
  TorrentDetailComponent,
  SearchComponent,
  APIComponent
} from './components';

import {
  MomentPipe,
  FileSizePipe,
  PeerHealthPipe
} from './pipes';

import {
  SearchService,
  TorrentDetailService,
} from './services';

import { AppRoutingModule } from './app-routing.module';
import {
  PaginationModule,
  TooltipModule
} from 'ngx-bootstrap';
import { FileUploadModule } from 'ng2-file-upload/ng2-file-upload';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    SearchComponent,
    FileSizePipe,
    MomentPipe,
    TorrentDetailComponent,
    APIComponent,
    PeerHealthPipe,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule,
    PaginationModule.forRoot(),
    TooltipModule.forRoot(),
    FileUploadModule
  ],
  providers: [SearchService, TorrentDetailService],
  bootstrap: [AppComponent]
})
export class AppModule { }
