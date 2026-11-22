import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { BaseProfile } from '../../routing/data/route-api';
import { FavoriteRoute } from '../data/favorite-api';

const PROFILE_LABELS: Record<BaseProfile, string> = {
  ROAD: 'szosowy',
  GRAVEL: 'gravel',
  MTB: 'górski',
};

@Component({
  selector: 'app-favorite-list',
  imports: [DatePipe, DecimalPipe, MatButtonModule],
  templateUrl: './favorite-list.html',
  styleUrl: './favorite-list.scss',
})
export class FavoriteList {
  readonly favorites = input.required<FavoriteRoute[]>();
  readonly opened = output<FavoriteRoute>();
  readonly removed = output<FavoriteRoute>();

  protected profileLabel(profile: BaseProfile): string {
    return PROFILE_LABELS[profile];
  }
}
