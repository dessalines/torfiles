import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent} from './home/home.component';
import { UploadComponent } from './upload/upload.component';
import { TorrentDetailComponent } from './torrent-detail/torrent-detail.component';

const routes: Routes = [
	{ path: '', redirectTo: '/home', pathMatch: 'full' },
	{ path: 'home', component: HomeComponent },
	{ path: 'upload', component: UploadComponent },
	{ path: 'torrent/:info_hash', component: TorrentDetailComponent }
];
@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})
export class AppRoutingModule { }
