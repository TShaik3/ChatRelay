import { beforeAll } from 'vitest';
import { setProjectAnnotations } from '@storybook/sveltekit';
import * as projectAnnotations from './preview';
import * as addonAnnotations from './preview';

// This is an important step to apply the right configuration when testing your stories.
// More info at: https://storybook.js.org/docs/api/portable-stories/portable-stories-vitest#setprojectannotations
const annotations = setProjectAnnotations([projectAnnotations, addonAnnotations]);
 
// Run Storybook's beforeAll hook
beforeAll(annotations.beforeAll);