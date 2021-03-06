package base;

import play.Application;
import play.ApplicationLoader.Context;
import play.Environment;
import play.db.Database;
import play.db.Databases;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.Helpers;
import repositories.JpaBadgeRepository;
import repositories.JpaRegionRepository;
import repositories.JpaTeamRepository;
import repositories.JpaTokenRepository;
import repositories.JpaUserRepository;
import repositories.interfaces.BadgeRepository;
import repositories.interfaces.RegionRepository;
import repositories.interfaces.TeamRepository;
import repositories.interfaces.TokenRepository;
import repositories.interfaces.UserRepository;
import services.BadgeServiceImpl;
import services.RegionServiceImpl;
import services.TeamServiceImpl;
import services.UserAuthServiceImpl;
import services.interfaces.BadgeService;
import services.interfaces.RegionService;
import services.interfaces.TeamService;
import services.interfaces.UserAuthService;

import javax.inject.Inject;

import lombok.Getter;

import org.junit.After;
import org.junit.Before;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Created by eduardo on 16/02/17.
 */
public class BaseTest
{
  @Inject
  Application application;
  @Getter
  Database database;

  @Before
  public void setup()
  {
    setupDatabase();
    setupTestModules();
    Helpers.start(this.application);
  }

  private void setupDatabase()
  {
    this.database = Databases.inMemory(
        "testDatabase",
        ImmutableMap.of(
            "MODE", "MYSQL"),
        ImmutableMap.of(
            "logStatements", true));
  }

  private void setupTestModules()
  {

    final GuiceApplicationBuilder builder = new GuiceApplicationLoader()
        .builder(new Context(Environment.simple()))
        .overrides(new AbstractModule()
        {
          @Override
          protected void configure()
          {
            // Repositories
            bind(UserRepository.class).to(JpaUserRepository.class).asEagerSingleton();
            bind(TokenRepository.class).to(JpaTokenRepository.class).asEagerSingleton();
            bind(TeamRepository.class).to(JpaTeamRepository.class).asEagerSingleton();
            bind(BadgeRepository.class).to(JpaBadgeRepository.class);
            bind(RegionRepository.class).to(JpaRegionRepository.class);
            bind(Database.class).toInstance(getDatabase());
            // Services
            bind(UserAuthService.class).to(UserAuthServiceImpl.class);
            bind(TeamService.class).to(TeamServiceImpl.class);
            bind(BadgeService.class).to(BadgeServiceImpl.class);
            bind(RegionService.class).to(RegionServiceImpl.class);
          }
        });
    Guice.createInjector(builder.applicationModule()).injectMembers(this);

  }

  @After
  public void teardown()
  {
    getDatabase().shutdown();
    Helpers.stop(this.application);
  }
}
