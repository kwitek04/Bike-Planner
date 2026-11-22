import { Component, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterLink } from '@angular/router';
import { FavoritesFacade } from '../favorites.facade';

export const MAX_NAME_LENGTH = 255;

@Component({
  selector: 'app-save-favorite-form',
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './save-favorite-form.html',
  styleUrl: './save-favorite-form.scss',
})
export class SaveFavoriteForm {
  protected readonly facade = inject(FavoritesFacade);
  protected readonly maxNameLength = MAX_NAME_LENGTH;

  protected readonly form = inject(NonNullableFormBuilder).group({
    name: [
      '',
      [Validators.required, Validators.maxLength(MAX_NAME_LENGTH), Validators.pattern(/.*\S.*/)],
    ],
  });

  constructor() {
    this.facade.resetSaveError();
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.facade.save(this.form.controls.name.value);
    this.form.reset();
  }
}
