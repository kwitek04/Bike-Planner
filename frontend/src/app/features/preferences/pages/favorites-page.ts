import { Component, inject } from '@angular/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { RouterLink } from '@angular/router';
import { FavoriteList } from '../components/favorite-list';
import { FavoriteRoute } from '../data/favorite-api';
import { FavoritesFacade } from '../favorites.facade';

@Component({
  selector: 'app-favorites-page',
  imports: [RouterLink, MatProgressBarModule, FavoriteList],
  templateUrl: './favorites-page.html',
  styleUrl: './favorites-page.scss',
})
export class FavoritesPage {
  protected readonly facade = inject(FavoritesFacade);

  constructor() {
    this.facade.load();
  }

  protected onOpened(favorite: FavoriteRoute): void {
    this.facade.open(favorite);
  }

  protected onRemoved(favorite: FavoriteRoute): void {
    this.facade.remove(favorite.id);
  }
}
