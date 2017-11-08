import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
	SearchComponent,
	TorrentDetailComponent,
	APIComponent
} from './components';


const routes: Routes = [
	{ path: '', redirectTo: '/search', pathMatch: 'full' },
	{ path: 'search', component: SearchComponent },
	{ path: 'api', component: APIComponent },
	{ path: 'torrent/:info_hash', component: TorrentDetailComponent }
];
@NgModule({
	imports: [RouterModule.forRoot(routes, { useHash: true })],
	exports: [RouterModule]
})
export class AppRoutingModule { }
