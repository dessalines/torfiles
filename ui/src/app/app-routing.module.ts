import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
	HomeComponent,
	UploadComponent,
	TorrentDetailComponent,
	ExportComponent
} from './components';


const routes: Routes = [
	{ path: '', redirectTo: '/home', pathMatch: 'full' },
	{ path: 'home', component: HomeComponent },
	{ path: 'upload', component: UploadComponent },
	{ path: 'export', component: ExportComponent },
	{ path: 'torrent/:info_hash', component: TorrentDetailComponent }
];
@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})
export class AppRoutingModule { }
