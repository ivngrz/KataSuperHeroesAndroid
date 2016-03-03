/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.ui.view.MainActivity;
import it.cosenonjaviste.daggermock.DaggerMockRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher.recyclerViewHasItemCount;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

  public static final int ANY_NUMBER_OF_SUPERHEROES = 8;
  @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @Test public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
  }

  @Test
  public void showListIfThereAreSuperHeroes() throws Exception {
    List<SuperHero> superHeros = givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPERHEROES);

    startActivity();

    onView(withId(R.id.recycler_view))
        .check(matches(recyclerViewHasItemCount(superHeros.size())));
  }

  private void givenThereAreNoSuperHeroes() {
    when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
  }

  private List<SuperHero> givenThereAreSomeSuperHeroes(int numberOfSuperHeroes){
    List<SuperHero> mockList = Collections.nCopies(numberOfSuperHeroes, buildMockSuperHero());

    when(repository.getAll()).thenReturn(mockList);

    return mockList;
  }

  private SuperHero buildMockSuperHero(){
    return new SuperHero("Scarlet Witch",
        "https://i.annihil.us/u/prod/marvel/i/mg/9/b0/537bc2375dfb9.jpg", false,
        "Scarlet Witch was born at the Wundagore base of the High Evolutionary, she and her twin "
            + "brother Pietro were the children of Romani couple Django and Marya Maximoff. The "
            + "High Evolutionary supposedly abducted the twins when they were babies and "
            + "experimented on them, once he was disgusted with the results, he returned them to"
            + " Wundagore, disguised as regular mutants.");
  }

  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
}