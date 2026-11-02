import { DecimalPipe, PercentPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { RoutingFacade } from '../routing.facade';

@Component({
  selector: 'app-planner-panel',
  imports: [
    DecimalPipe,
    PercentPipe,
    MatButtonModule,
    MatButtonToggleModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatSelectModule,
  ],
  templateUrl: './planner-panel.html',
  styleUrl: './planner-panel.scss',
})
export class PlannerPanel {
  protected readonly facade = inject(RoutingFacade);
}
